import type { LoginRequest } from "@/types/login/LoginRequest";
import { BaseRepository } from "./base.repository";
import type { LoginResponse } from "@/types/login/LoginResponse";
import type { SignupRequest } from "@/types/signup/SignupRequest";
import type { CreateUserResponse } from "@/types/signup/CreateUserResponse";

export class AuthRepository extends BaseRepository {
  private static instance: AuthRepository;

  private constructor() {
    super();
  }

  public static getInstance(): AuthRepository {
    if (!AuthRepository.instance) {
      AuthRepository.instance = new AuthRepository();
    }
    return AuthRepository.instance;
  }

  async login(data: LoginRequest): Promise<LoginResponse> {
    const response = await this.post<LoginResponse>("/api/auth/login", data);
    localStorage.setItem("token", response.data.token);
    return response.data;
  }

  async signup(data: SignupRequest): Promise<LoginResponse> {
    const createUserResponse = await this.post<CreateUserResponse>(
      "/app_user/create",
      data
    );

    const loginRequest: LoginRequest = {
      email: createUserResponse.data.email,
      password: "todo: パスワードを生成する",
    };
    const response = await this.post<LoginResponse>(
      "/auth/login",
      loginRequest
    );
    localStorage.setItem("token", response.data.token);
    return response.data;
  }

  async logout(): Promise<void> {
    await this.post("/auth/logout");
    localStorage.removeItem("token");
  }

  async getCurrentUser(): Promise<LoginResponse> {
    return (await this.get<LoginResponse>("/auth/me")).data;
  }
}
