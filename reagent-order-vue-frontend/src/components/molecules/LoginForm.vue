<template>
  <div class="space-y-6">
    <div class="space-y-4">
      <BaseInput
        id="email"
        label="メールアドレス"
        type="email"
        v-model="email"
        placeholder="example@example.com"
      />
      <BaseInput
        id="password"
        label="パスワード"
        type="password"
        v-model="password"
        placeholder="********"
      />
    </div>

    <div class="flex items-center justify-between">
      <label class="flex items-center">
        <input
          type="checkbox"
          class="w-4 h-4 text-blue-600 rounded border-gray-300 focus:ring-blue-500"
        />
        <span class="ml-2 text-sm text-gray-600">ログイン状態を保持</span>
      </label>
      <a href="#" class="text-sm text-blue-600 hover:text-blue-800"
        >パスワードをお忘れですか？</a
      >
    </div>

    <button
      type="submit"
      @click="handleSubmit"
      class="w-full py-3 px-4 bg-gradient-to-r from-blue-600 to-blue-700 text-white rounded-lg hover:from-blue-700 hover:to-blue-800 focus:outline-none focus:ring-2 focus:ring-blue-500/20 transform transition-all duration-200 ease-in-out hover:scale-[1.02] shadow-lg hover:shadow-xl"
    >
      ログイン
    </button>

    <div class="relative">
      <div class="absolute inset-0 flex items-center">
        <div class="w-full border-t border-gray-300"></div>
      </div>
      <div class="relative flex justify-center text-sm">
        <span class="px-2 bg-white text-gray-500">または</span>
      </div>
    </div>

    <div class="grid grid-cols-2 gap-4">
      <button
        type="button"
        class="flex items-center justify-center px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-all duration-200"
      >
        <img
          src="https://www.google.com/favicon.ico"
          alt="Google"
          class="w-5 h-5 mr-2"
        />
        <span class="text-sm font-medium text-gray-700">Google</span>
      </button>
      <button
        type="button"
        class="flex items-center justify-center px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-all duration-200"
      >
        <img
          src="https://github.com/favicon.ico"
          alt="GitHub"
          class="w-5 h-5 mr-2"
        />
        <span class="text-sm font-medium text-gray-700">GitHub</span>
      </button>
    </div>

    <p class="text-center text-sm text-gray-600">
      アカウントをお持ちでない方は
      <a href="/signup" class="font-medium text-blue-600 hover:text-blue-800"
        >新規登録</a
      >
    </p>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import BaseInput from "../atoms/input/BaseInput.vue";

const router = useRouter();
const authStore = useAuthStore();

const email = ref("");
const password = ref("");

async function handleSubmit() {
  try {
    await authStore.login({
      email: email.value,
      password: password.value,
    });
    router.push("/home");
  } catch (error) {
    console.error("Login failed:", error);
  }
}
</script>
