<template>
  <div class="space-y-6">
    <div class="space-y-4">
      <BaseInput
        id="name"
        label="お名前"
        type="text"
        v-model="name"
        placeholder="山田 太郎"
      />
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
      <BaseInput
        id="passwordConfirm"
        label="パスワード（確認）"
        type="password"
        v-model="passwordConfirm"
        placeholder="********"
      />
    </div>

    <button
      type="submit"
      @click="handleSubmit"
      :disabled="!isFormValid"
      class="w-full py-3 px-4 bg-gradient-to-r from-blue-600 to-blue-700 text-white rounded-lg hover:from-blue-700 hover:to-blue-800 focus:outline-none focus:ring-2 focus:ring-blue-500/20 transform transition-all duration-200 ease-in-out hover:scale-[1.02] shadow-lg hover:shadow-xl disabled:opacity-50 disabled:cursor-not-allowed"
    >
      アカウントを作成
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
      すでにアカウントをお持ちの方は
      <a href="/login" class="font-medium text-blue-600 hover:text-blue-800"
        >ログイン</a
      >
    </p>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import BaseInput from "../atoms/input/BaseInput.vue";
import { useAuthStore } from "@/stores/auth";
import { useRouter } from "vue-router";

const router = useRouter();
const authStore = useAuthStore();

const name = ref("");
const email = ref("");
const password = ref("");
const passwordConfirm = ref("");

const isFormValid = computed(() => {
  return (
    name.value.length > 0 &&
    email.value.length > 0 &&
    password.value.length >= 8 &&
    password.value === passwordConfirm.value
  );
});

async function handleSubmit() {
  if (!isFormValid.value) return;

  try {
    await authStore.signup({
      appUserName: name.value,
      email: email.value,
      password: password.value,
    });

    router.push("/home");
  } catch (e) {
    console.error(e);
  }
}
</script>
