import { create } from "zustand";

const useStore = create((set) => ({
  token: '',
  username: '',
  selectedPage: 2,
  setToken: (newToken) => set(() => ({ token: newToken })),
  setUsername: (newUsername) => set(() => ({ username: newUsername })),
  setSelectedPage: (newSelectedPage) => set(() => ({ selectedPage: newSelectedPage })),
}));

export default useStore;