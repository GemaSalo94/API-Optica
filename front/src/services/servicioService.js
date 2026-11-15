import api from "./api";

export const listarServicios = async () => {
  const { data } = await api.get("/servicios");
  return data;
};

export const crearServicio = async (servicio) => {
  const { data } = await api.post("/servicios", servicio);
  return data;
};

export const actualizarServicio = async (id, servicio) => {
  const { data } = await api.put(`/servicios/${id}`, servicio);
  return data;
};

export const eliminarServicio = async (id) => {
  await api.delete(`/servicios/${id}`);
};
