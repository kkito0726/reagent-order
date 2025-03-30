import type { AppUserEntity } from "../core/AppUserEntity";

export type LoginResponse = {
  user: AppUserEntity;
  token: string;
};
