import axios from 'axios';

const BASE_URL = 'http://localhost:8080';

const ApiService = {

  setAuthToken: token => {
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  },

  get: async (url, params = {}) => {
    try {
      const response = await axios.get(`${BASE_URL}${url}`, { params });
      return response.data;
    } catch (error) {
      throw error.response ? error.response.data : error.message;
    }
  },

  post: async (url, data = {}) => {
    try {
      const response = await axios.post(`${BASE_URL}${url}`, data);
      return response.data;
    } catch (error) {
      throw error.response ? error.response.data : error.message;
    }
  },
  
};

export default ApiService;