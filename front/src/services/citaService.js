import api from "./api";

export const listarCitas = async (filtros = {}) => {
  const { data } = await api.get("/citas", { params: filtros });
  return data;
};

export const obtenerCita = async (id) => {
  const { data } = await api.get(`/citas/${id}`);
  return data;
};

export const crearCita = async (cita) => {
  const { data } = await api.post("/citas", cita);
  return data;
};

export const actualizarCita = async (id, cita) => {
  const { data } = await api.put(`/citas/${id}`, cita);
  return data;
};
export const obtenerDisponibilidad = async (fecha, idServicio) => {
  const { data } = await api.get("/citas/disponibilidad", {
    params: { fecha, idServicio },
  });
  return data;
};
export const cambiarEstadoCita = async (id, estado) => {
  const { data } = await api.patch(`/citas/${id}/estado`, { estado });
  return data;
};

export const eliminarCita = async (id) => {
  await api.delete(`/citas/${id}`);
};
