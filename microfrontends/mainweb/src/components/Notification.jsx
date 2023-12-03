import React, { useState, useEffect } from 'react';
import { toast } from "react-toastify";
import useStore from "sideweb/store";
import SockJS from "sockjs-client";
// import { over } from "stompjs";

let stompClient = null;

const Notification = () => {
    const { token: storeToken } = useStore();

    // useEffect(() => {
    //     let sock = new SockJS(`http://localhost:8083/notification/record?token=${storeToken}`);
    //     stompClient = over(sock);
    //     stompClient.connect({}, onConnected, onError);

    //     const onConnected = () => {
    //         stompClient.subscribe('/topic/records', onMessageReceived);
    //     }

    //     const onError = () => {}

    //     const onMessageReceived = (payload) => {
    //         let jsonPayload = JSON.parse(payload.body);
    //         toast.info(`${jsonPayload.username} set a new high score of ${jsonPayload.points} points!`, { position: "bottom-right" });
    //     }
    // }, []);

    return (
        <div></div>
    )

}

export default Notification;