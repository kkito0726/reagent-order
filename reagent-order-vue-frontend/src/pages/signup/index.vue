<template>
  <div
    class="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 via-white to-blue-50 py-12 px-4 sm:px-6 lg:px-8"
  >
    <div class="max-w-md w-full space-y-8">
      <div class="text-center">
        <h2
          class="mt-6 text-3xl font-extrabold bg-gradient-to-r from-blue-600 to-blue-800 bg-clip-text text-transparent"
        >
          新規登録
        </h2>
        <p class="mt-2 text-sm text-gray-600">
          アカウントを作成して始めましょう
        </p>
      </div>
      <div class="bg-white p-8 rounded-2xl shadow-xl border border-gray-100">
        <SignupForm />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import SignupForm from "@/components/molecules/SignupForm.vue";
</script>

<style scoped>
.signup-page {
  padding: 20px;
}
</style>
