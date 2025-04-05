import { ref } from "vue";

export function usePasswordToggle() {
  const showPassword = ref(false);
  const toggle = () => {
    showPassword.value = !showPassword.value;
  };

  return {
    showPassword,
    toggle,
  };
}
