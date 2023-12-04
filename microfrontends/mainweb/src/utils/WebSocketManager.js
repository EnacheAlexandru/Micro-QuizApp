import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { toast } from "react-toastify";

class WebSocketManager {
  constructor() {
    this.client = new Client();
    this.client.webSocketFactory = () =>
      new SockJS("http://localhost:8083/notification/record");

    this.client.configure({
      onConnect: () => {
        this.client.subscribe("/topic/records", (payload) => {
          const jsonPayload = JSON.parse(payload.body);
          console.log(jsonPayload);
          toast.info(
            `${jsonPayload.username} set a new high score of ${jsonPayload.points} points!`,
            { position: "bottom-right" }
          );
        });
        console.log("WebSocket connected");
      },
      onDisconnect: () => {
        console.log("WebSocket disconnected");
      },
    });
  }

  connect(token) {
    if (token !== undefined && token !== null && token !== "" && !this.client.connected) {
      this.client.configure({
        webSocketFactory: () =>
          new SockJS(
            `http://localhost:8083/notification/record?token=${token}`
          ),
      });
      this.client.activate();
    }
  }

  disconnect() {
    if (this.client.connected) {
        this.client.deactivate();
    }
  }
}

const webSocketManager = new WebSocketManager();
export default webSocketManager;
