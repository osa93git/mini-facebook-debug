import {BrowserRouter, Routes, Route} from "react-router-dom";
import LoginPage from "../pages/LoginPage";

const AppRouter = () => {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/login" element={<LoginPage />} />
            </Routes>
        </BrowserRouter>
    );
};

export default  AppRouter;