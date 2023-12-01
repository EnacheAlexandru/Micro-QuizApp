import { create } from "zustand";

const useStore = create((set) => ({
  token: 'lol',
  setToken: (newToken) => set(() => ({ token: newToken })),
}));

export default useStore;