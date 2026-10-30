import axios from "axios";

export const api = axios.create({
  baseURL: "/api", // Vite 프록시 활용 시, 프론트엔드에서 /api로 시작하는 요청은 백엔드 서버로 프록시됨
  withCredentials: true,
  timeout: 15000,
});

interface CsrfResponse {
  headerName: string;
  parameterName: string;
  token: string;
}

export async function getCsrfHeaders() {
  const { data } = await api.get<CsrfResponse>("/auth/csrf");
  return { [data.headerName]: data.token };
}
