<template>
  <div class="w-full">
    <label :for="id" class="block text-sm font-medium text-gray-700 mb-1.5">
      {{ label }}
    </label>
    <input
      :id="id"
      :type="type"
      :value="modelValue"
      @input="
        $emit('update:modelValue', ($event.target as HTMLInputElement).value)
      "
      class="w-full px-4 py-3 bg-white border border-gray-200 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-all duration-200 ease-in-out placeholder:text-gray-400"
      :placeholder="placeholder"
    />
  </div>
</template>

<script setup lang="ts">
interface Props {
  id: string;
  label: string;
  type?: string;
  modelValue: string;
  placeholder?: string;
}

defineProps<Props>();
defineEmits<{
  (e: "update:modelValue", value: string): void;
}>();
</script>
