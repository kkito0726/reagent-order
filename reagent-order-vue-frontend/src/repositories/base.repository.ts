import type { ApiResponse } from "@/types/core/api";
import axios, { type AxiosInstance, type AxiosRequestConfig } from "axios";

export class BaseRepository {
  protected readonly axios: AxiosInstance;
  protected readonly baseURL: string;

  constructor(baseURL: string = import.meta.env.VITE_API_BASE_URL) {
    this.baseURL = baseURL;
    this.axios = axios.create({
      baseURL,
      headers: {
        "Content-Type": "application/json",
      },
    });

    this.setupInterceptors();
  }

  private setupInterceptors(): void {
    this.axios.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem("token");
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    this.axios.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response?.status === 401) {
          localStorage.removeItem("token");
          window.location.href = "/login";
        }
        return Promise.reject(error);
      }
    );
  }

  protected async get<T>(
    path: string,
    config?: AxiosRequestConfig
  ): Promise<ApiResponse<T>> {
    const response = await this.axios.get<T>(path, config);
    return {
      data: response.data,
      status: response.status,
    };
  }

  protected async post<T>(
    path: string,
    data?: unknown,
    config?: AxiosRequestConfig
  ): Promise<ApiResponse<T>> {
    const response = await this.axios.post<T>(path, data, config);
    return {
      data: response.data,
      status: response.status,
    };
  }

  protected async put<T>(
    path: string,
    data?: unknown,
    config?: AxiosRequestConfig
  ): Promise<ApiResponse<T>> {
    const response = await this.axios.put<T>(path, data, config);
    return {
      data: response.data,
      status: response.status,
    };
  }

  protected async delete<T>(
    path: string,
    config?: AxiosRequestConfig
  ): Promise<ApiResponse<T>> {
    const response = await this.axios.delete<T>(path, config);
    return {
      data: response.data,
      status: response.status,
    };
  }
}
