import api from "./api";

export const listarTramos = async (dia) => {
  const { data } = await api.get("/tramos", { params: dia ? { dia } : {} });
  return data;
};

export const crearTramo = async (tramo) => {
  const { data } = await api.post("/tramos", tramo);
  return data;
};

export const eliminarTramo = async (id) => {
  await api.delete(`/tramos/${id}`);
};
