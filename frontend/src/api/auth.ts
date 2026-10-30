import { api, getCsrfHeaders } from "./client";

export interface MeResponse {
  username: string;
  gold: number;
}

export interface SignupResponse {
  username: string;
}

export async function signup(
  username: string,
  password: string,
): Promise<SignupResponse> {
  const { data } = await api.post<SignupResponse>(
    "/auth/signup",
    {
      username,
      password,
    },
    {
      headers: await getCsrfHeaders(),
    },
  );
  return data;
}

export async function login(username: string, password: string) {
  const params = new URLSearchParams({
    username,
    password,
  });
  await api.post("/auth/login", params, {
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
      ...(await getCsrfHeaders()),
    },
  });
}

export async function getMe(): Promise<MeResponse> {
  const { data } = await api.get<MeResponse>("/auth/me");
  return data;
}

export async function logout() {
  await api.post("/auth/logout", undefined, {
    headers: await getCsrfHeaders(),
  });
}
