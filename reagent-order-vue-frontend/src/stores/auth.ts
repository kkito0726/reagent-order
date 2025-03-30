import { defineStore } from "pinia";
import { ref } from "vue";

export const useAuthStore = defineStore("auth", () => {
  const isAuthenticated = ref(false);
  const user = ref<{
    id: string;
    name: string;
    email: string;
  } | null>(null);

  function login(userData: { email: string; password: string }) {
    // TODO: 実際のAPI認証を実装
    isAuthenticated.value = true;
    user.value = {
      id: "1",
      name: "テストユーザー",
      email: userData.email,
    };
  }

  function logout() {
    isAuthenticated.value = false;
    user.value = null;
  }

  return {
    isAuthenticated,
    user,
    login,
    logout,
  };
});
