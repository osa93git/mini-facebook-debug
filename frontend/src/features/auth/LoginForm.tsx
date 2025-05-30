import {useState} from "react";
import {useNavigate} from "react-router-dom";
import {login} from "../../api/authApi.ts";
import type { LoginRequest } from "../../api/authApi.ts";

const LoginForm = () => {
    const navigate = useNavigate();

    const[email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);


    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);
        setLoading(true);

        const credentials: LoginRequest = {email, password};

        try{
            const response = await login(credentials);
            localStorage.setItem("accessToken", response.accessToken);
            navigate("/profile/me");
        } catch (err: unknown) {
            setError("email or password error");
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            <h2>Login</h2>

            <div>
                <label>Email:</label>
                <input
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                />
            </div>
            <div>
                <label>Password:</label>
                <input
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />
            </div>

            {error&&<p style={{ color: "red"}}>{error}</p>}

            <button type="submit" disabled={loading}>
                {loading ? "Logowanie..." : "Zaloguj się"}
            </button>
        </form>
    );
};

export default LoginForm;