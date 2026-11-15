import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import {
  listarCitas,
  crearCita,
  obtenerDisponibilidad,
  cambiarEstadoCita,
  eliminarCita,
} from "../services/citaService";
import { listarClientes, miPerfil } from "../services/clienteService";
import { listarServicios } from "../services/servicioService";

const FORM_INICIAL = { idCliente: "", idServicio: "", fecha: "", hora: "" };

export default function Citas() {
  const { usuario, logout } = useAuth();
  const esCliente = usuario?.rol === "CLIENTE";

  const [citas, setCitas] = useState([]);
  const [clientes, setClientes] = useState([]);
  const [servicios, setServicios] = useState([]);
  const [clientePropio, setClientePropio] = useState(null);
  const [horas, setHoras] = useState([]);
  const [form, setForm] = useState(FORM_INICIAL);
  const [mostrarForm, setMostrarForm] = useState(false);
  const [error, setError] = useState("");
  const [cargando, setCargando] = useState(true);

  const cargarCitas = () => listarCitas().then(setCitas);

  useEffect(() => {
    const tareas = [cargarCitas(), listarServicios().then(setServicios)];

    if (esCliente) {
      tareas.push(miPerfil().then(setClientePropio));
    } else {
      tareas.push(listarClientes().then(setClientes));
    }

    Promise.all(tareas)
      .catch(() => setError("Error cargando datos"))
      .finally(() => setCargando(false));
  }, [esCliente]);

  // Pedir huecos cuando cambia fecha o servicio
  useEffect(() => {
    if (!form.fecha || !form.idServicio) return;

    let cancelado = false;

    obtenerDisponibilidad(form.fecha, Number(form.idServicio))
      .then((h) => {
        if (cancelado) return;
        setHoras(h);
        setForm((f) => ({ ...f, hora: "" }));
      })
      .catch(() => {
        if (!cancelado) setHoras([]);
      });

    return () => {
      cancelado = true;
    };
  }, [form.fecha, form.idServicio]);

  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      await crearCita({
        idCliente: esCliente ? clientePropio.id : Number(form.idCliente),
        idServicio: Number(form.idServicio),
        fechaHora: `${form.fecha}T${form.hora}:00`,
        estado: "PENDIENTE",
      });
      setForm(FORM_INICIAL);
      setHoras([]);
      setMostrarForm(false);
      await cargarCitas();
    } catch (err) {
      const data = err.response?.data;
      setError(
        typeof data === "object"
          ? Object.values(data).join(" · ")
          : "Error al crear la cita",
      );
    }
  };

  const handleEstado = async (id, estado) => {
    if (
      estado === "CANCELADA" &&
      !window.confirm("¿Estás seguro de que deseas cancelar esta cita?")
    ) {
      return;
    }

    setError("");
    try {
      await cambiarEstadoCita(id, estado);
      await cargarCitas();
    } catch (err) {
      const data = err.response?.data;
      setError(
        typeof data === "object" ? data.message : "Error al cambiar el estado",
      );
    }
  };

  const handleEliminar = async (id) => {
    if (!window.confirm("¿Estás seguro de que deseas eliminar esta cita?")) {
      return;
    }

    setError("");
    try {
      await eliminarCita(id);
      await cargarCitas();
    } catch {
      setError("Error al eliminar la cita");
    }
  };

  return (
    <div className="p-8 max-w-5xl mx-auto">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">Citas</h1>
        <div className="flex items-center gap-4">
          <span className="text-sm text-slate-600">{usuario?.email}</span>
          <button
            onClick={logout}
            className="bg-red-600 text-white px-3 py-1 rounded hover:bg-red-700"
          >
            Cerrar sesión
          </button>
        </div>
      </div>

      <button
        onClick={() => setMostrarForm(!mostrarForm)}
        className="mb-4 bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
      >
        {mostrarForm ? "Cancelar" : "Nueva cita"}
      </button>

      {error && <p className="text-red-600 mb-4">{error}</p>}

      {mostrarForm && (
        <form
          onSubmit={handleSubmit}
          className="bg-white p-4 rounded shadow mb-6 space-y-3"
        >
          {esCliente ? (
            <p className="text-sm text-slate-600">
              Cita para:{" "}
              <strong>
                {clientePropio?.nombre} {clientePropio?.apellidos}
              </strong>
            </p>
          ) : (
            <select
              name="idCliente"
              value={form.idCliente}
              onChange={handleChange}
              required
              className="w-full border rounded px-3 py-2"
            >
              <option value="">Selecciona cliente</option>
              {clientes.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.nombre} {c.apellidos}
                </option>
              ))}
            </select>
          )}

          <select
            name="idServicio"
            value={form.idServicio}
            onChange={handleChange}
            required
            className="w-full border rounded px-3 py-2"
          >
            <option value="">Selecciona servicio</option>
            {servicios.map((s) => (
              <option key={s.id} value={s.id}>
                {s.nombre} ({s.duracionMin} min)
              </option>
            ))}
          </select>

          <input
            type="date"
            name="fecha"
            value={form.fecha}
            onChange={handleChange}
            required
            className="w-full border rounded px-3 py-2"
          />

          <select
            name="hora"
            value={form.hora}
            onChange={handleChange}
            required
            disabled={!form.fecha || !form.idServicio}
            className="w-full border rounded px-3 py-2 disabled:bg-slate-100"
          >
            <option value="">
              {!form.fecha || !form.idServicio
                ? "Elige servicio y fecha"
                : horas.length === 0
                  ? "No hay disponibilidad"
                  : "Selecciona hora"}
            </option>
            {horas.map((h) => (
              <option key={h} value={h}>
                {h}
              </option>
            ))}
          </select>

          <button
            type="submit"
            className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
          >
            Guardar cita
          </button>
        </form>
      )}

      {cargando ? (
        <p className="text-slate-500">Cargando...</p>
      ) : (
        <table className="w-full text-sm border-collapse">
          <thead>
            <tr className="bg-slate-200 text-left">
              <th className="p-2 border">ID</th>
              <th className="p-2 border">Cliente</th>
              <th className="p-2 border">Servicio</th>
              <th className="p-2 border">Inicio</th>
              <th className="p-2 border">Fin</th>
              <th className="p-2 border">Estado</th>
              <th className="p-2 border">Acciones</th>
            </tr>
          </thead>
          <tbody>
            {citas.map((c) => (
              <tr key={c.id} className="hover:bg-slate-50">
                <td className="p-2 border">{c.id}</td>
                <td className="p-2 border">{c.nombreCliente}</td>
                <td className="p-2 border">{c.nombreServicio}</td>
                <td className="p-2 border">
                  {new Date(c.fechaHora).toLocaleString("es-ES")}
                </td>
                <td className="p-2 border">
                  {new Date(c.horaFin).toLocaleString("es-ES")}
                </td>
                <td className="p-2 border">{c.estado}</td>
                <td className="p-2 border space-x-2 whitespace-nowrap">
                  {esCliente ? (
                    c.estado === "PENDIENTE" && (
                      <button
                        onClick={() => handleEstado(c.id, "CANCELADA")}
                        className="text-red-600 hover:underline"
                      >
                        Cancelar
                      </button>
                    )
                  ) : (
                    <>
                      {c.estado === "PENDIENTE" && (
                        <>
                          <button
                            onClick={() => handleEstado(c.id, "COMPLETADA")}
                            className="text-green-600 hover:underline"
                          >
                            Completar
                          </button>
                          <button
                            onClick={() => handleEstado(c.id, "CANCELADA")}
                            className="text-orange-600 hover:underline"
                          >
                            Cancelar
                          </button>
                        </>
                      )}
                      <button
                        onClick={() => handleEliminar(c.id)}
                        className="text-red-700 hover:opacity-70"
                        title="Eliminar cita"
                      >
                        🗑️
                      </button>
                    </>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
