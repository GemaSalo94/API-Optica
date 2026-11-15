import api from "./api";

export const listarClientes = async () => {
  const { data } = await api.get("/clientes");
  return data;
};

export const obtenerCliente = async (id) => {
  const { data } = await api.get(`/clientes/${id}`);
  return data;
};

export const crearCliente = async (cliente) => {
  const { data } = await api.post("/clientes", cliente);
  return data;
};
export const miPerfil = async () => {
  const { data } = await api.get("/clientes/mi-perfil");
  return data;
};
export const actualizarCliente = async (id, cliente) => {
  const { data } = await api.put(`/clientes/${id}`, cliente);
  return data;
};

export const eliminarCliente = async (id) => {
  await api.delete(`/clientes/${id}`);
};
