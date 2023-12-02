import React, { useEffect, useState } from "react";
import Header from "../components/Header";
import { ProgressBar } from "react-loader-spinner";
import useStore from "sideweb/store";
import axios from "axios";

const AnsweredPage = () => {
  const { token: storeToken } = useStore();
  const FETCH_LIST_ERROR_MSG = "Error fetching list or session expired";
  const [isErrorFetch, setIsErrorFetch] = useState(false);

  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    handleGetAnswered();
  }, []);

  const handleGetAnswered = async () => {
    setIsLoading(true);
    await new Promise((resolve) => setTimeout(resolve, 1000));
    await axios
      .get("http://localhost:8080/question/other/answer", {
        headers: { Authorization: `Bearer ${storeToken}` },
      })
      .then((response) => {
        console.log(response);
        setIsErrorFetch(false);
      })
      .catch((error) => {
        console.log(error);
        setIsErrorFetch(true);
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  return (
    <div>
      <Header />
      <div className="m-5 flex justify-center">
        <button
          onClick={handleGetAnswered}
          className="text-white bg-blue-500 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm sm:w-auto px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
        >
          Refreshh
        </button>
      </div>
      {isLoading ? (
        <div className="m-5 flex justify-center">
          <ProgressBar
            height="80"
            width="80"
            ariaLabel="progress-bar-loading"
            wrapperClass="progress-bar-wrapper"
            borderColor="#1D4ED8"
            barColor="#51E5FF"
          />
        </div>
      ) : isErrorFetch ? (
        <div className="m-5 text-red-600 flex justify-center">
          {FETCH_LIST_ERROR_MSG}
        </div>
      ) : (
        <div className="m-5 flex justify-center">
          <div>
            <input type="radio" value="Male" name="0" /> Male
            <input type="radio" value="Female" name="0" /> Female
            <input type="radio" value="Other" name="0" /> Other
          </div>
          <div>
            <input type="radio" value="Male" name="1" /> Malee
            <input type="radio" value="Female" name="1" /> Femalee
            <input type="radio" value="Other" name="1" /> Otherr
          </div>
        </div>
      )}
    </div>
  );
};

export default AnsweredPage;
