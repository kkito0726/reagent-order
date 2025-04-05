import { defineStore } from "pinia";
import { ref } from "vue";
import type { LoginRequest } from "@/types/login/LoginRequest";
import type { LoginResponse } from "@/types/login/LoginResponse";
import { AuthRepository } from "@/repositories/auth.repository";
import type { SignupRequest } from "@/types/signup/SignupRequest";

export const useAuthStore = defineStore("auth", () => {
  const isAuthenticated = ref(false);
  const user = ref<LoginResponse | null>(null);
  const authRepository = AuthRepository.getInstance();

  async function login(loginRequest: LoginRequest) {
    try {
      const response = await authRepository.login(loginRequest);
      isAuthenticated.value = true;
      user.value = response;
      return response;
    } catch (error) {
      console.error("Login failed:", error);
      throw error;
    }
  }

  const signup = async (signupRequest: SignupRequest) => {
    try {
      const response = await authRepository.signup(signupRequest);
      isAuthenticated.value = true;
      user.value = response;
    } catch (error) {
      console.error("Login failed:", error);
      throw error;
    }
  };

  function logout() {
    authRepository.logout();
    isAuthenticated.value = false;
    user.value = null;
  }

  return {
    isAuthenticated,
    user,
    login,
    signup,
    logout,
  };
});
