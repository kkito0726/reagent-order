import {
  createRouter,
  createWebHistory,
  type RouteRecordRaw,
} from "vue-router";
import { useAuthStore } from "@/stores/auth";

const routes: RouteRecordRaw[] = [
  {
    path: "/",
    redirect: "/home",
  },
  {
    path: "/login",
    name: "login",
    component: () => import("@/pages/login/index.vue"),
    meta: { requiresGuest: true },
  },
  {
    path: "/signup",
    name: "signup",
    component: () => import("@/pages/signup/index.vue"),
    meta: { requiresGuest: true },
  },
  {
    path: "/home",
    name: "home",
    component: () => import("@/pages/home/index.vue"),
    meta: { requiresAuth: true },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next("/login");
  } else if (to.meta.requiresGuest && authStore.isAuthenticated) {
    next("/home");
  } else {
    next();
  }
});

export default router;
