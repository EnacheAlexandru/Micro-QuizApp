import React from "react";
import SockJS from "sockjs-client";
import { over } from "stompjs";
import ApiService from "./ApiService";

let stompClient = null;
const jwt = 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkZWZhdWx0IiwiaWF0IjoxNzAxMjkwMjUxLCJleHAiOjE3MDEyOTM4NTF9.NytyHgJjpHeoPfYS7Ykm12ktdGkysl7mMQCSAK6QOktiftXxMN7jUKg3iW9afjytNYT8ad4gfQ5QhZyJFJNoEg';

const WebSocketApp = () => {

    const connectToWebSocket = () => {
        let Sock = new SockJS('http://localhost:8082/leaderboard?token=' + jwt);
        stompClient = over(Sock);
        stompClient.connect({}, onConnected, onError);
    }

    const onConnected = () => {
        stompClient.subscribe('/topic/records', onMessageReceived);
    }

    const onError = (err) => {
        // nothing
    }

    const onMessageReceived = (payload) => {
        let JsonPayload = JSON.parse(payload.body);
        console.log("From server: " + JsonPayload.username);
        console.log("From server: " + JsonPayload.points);
    }

    const sendToWebSocket = () => {
        let request = {
            username: 'verstappen',
            points: 500
        };

        let requestJson = JSON.stringify(request);

        stompClient.send('/app/record', {}, requestJson);
    }

    const getList = () => {
        ApiService.setAuthToken(jwt);

        ApiService.get('/leaderboard/list')
          .then(response => {
            console.log(response);
          })
          .catch(error => {
            console.error('Error fetching data:', error);
          });
    }

    return (
        <div>
            <div>
                hello
            </div>
            <div>
                <button type="button" onClick={connectToWebSocket}>
                    Connect
                </button>
            </div>
            <div>
                <button type="button" onClick={sendToWebSocket}>
                    Send
                </button>
            </div>
            <div>
                <button type="button" onClick={getList}>
                    GetList
                </button>
            </div>
        </div>
    )
}

export default WebSocketApp