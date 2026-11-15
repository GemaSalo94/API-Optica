import { Routes, Route, Navigate } from "react-router-dom";
import Login from "./views/Login";
import Registro from "./views/Registro";
import Citas from "./views/Citas";
import RutaProtegida from "./components/RutaProtegida";

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/registro" element={<Registro />} />
      <Route
        path="/citas"
        element={
          <RutaProtegida>
            <Citas />
          </RutaProtegida>
        }
      />
      <Route path="*" element={<Navigate to="/citas" replace />} />
    </Routes>
  );
}
