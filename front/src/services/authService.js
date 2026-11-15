import api from "./api";

export const login = async ({ email, password }) => {
  const { data } = await api.post("/auth/login", { email, password });
  return data; // { token, email, rol }
};

export const registro = async (datos) => {
  const { data } = await api.post("/auth/registro", datos);
  return data; // { token, email, rol }
};
