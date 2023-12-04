import { create } from "zustand";

const useStore = create((set) => ({
  // token: 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJsZWNsZXJjIiwiaWF0IjoxNzAxNjk0MjU3LCJleHAiOjE3MDE2OTc4NTd9.enNB_vKhHwC4PciIpHWo9LOYfGV_uk9EIqyn-UIuLx1PU_F-wpg2ZUtwRySS0jDrspLyFmTGgweBnaSWZEC2-A',
  token: '',
  username: '',
  selectedPage: 2,
  setToken: (newToken) => set(() => ({ token: newToken })),
  setUsername: (newUsername) => set(() => ({ username: newUsername })),
  setSelectedPage: (newSelectedPage) => set(() => ({ selectedPage: newSelectedPage })),
}));

export default useStore;