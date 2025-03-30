<template>
  <div
    class="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 via-white to-blue-50 py-12 px-4 sm:px-6 lg:px-8"
  >
    <div class="max-w-md w-full space-y-8">
      <div class="text-center">
        <h2
          class="mt-6 text-3xl font-extrabold bg-gradient-to-r from-blue-600 to-blue-800 bg-clip-text text-transparent"
        >
          ログイン
        </h2>
        <p class="mt-2 text-sm text-gray-600">アカウントにログインして続ける</p>
      </div>
      <div class="bg-white p-8 rounded-2xl shadow-xl border border-gray-100">
        <LoginForm />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import LoginForm from "@/components/molecules/LoginForm.vue";
</script>

<style scoped>
.login-page {
  padding: 20px;
}
</style>
