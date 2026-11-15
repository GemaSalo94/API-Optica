/* eslint-disable react-refresh/only-export-components */

import { createContext, useContext, useState } from "react";
import * as authService from "../services/authService";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(() => {
    const token = localStorage.getItem("token");
    const email = localStorage.getItem("email");
    const rol = localStorage.getItem("rol");
    return token ? { token, email, rol } : null;
  });

  const guardarSesion = (data) => {
    localStorage.setItem("token", data.token);
    localStorage.setItem("email", data.email);
    localStorage.setItem("rol", data.rol);
    setUsuario(data);
  };

  const login = async (credenciales) => {
    const data = await authService.login(credenciales);
    guardarSesion(data);
    return data;
  };

  const registro = async (datos) => {
    const data = await authService.registro(datos);
    guardarSesion(data);
    return data;
  };

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("email");
    localStorage.removeItem("rol");
    setUsuario(null);
  };

  return (
    <AuthContext.Provider value={{ usuario, login, registro, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth debe usarse dentro de <AuthProvider>");
  return ctx;
}
