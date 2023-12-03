import React, { useEffect, useState } from "react";
import Header from "../components/Header";
import { ProgressBar } from "react-loader-spinner";
import useStore from "sideweb/store";
import axios from "axios";
import { useParams } from "react-router-dom";

const MyQuestionDetailsPage = () => {
  const { questionId } = useParams();

  const { token: storeToken } = useStore();

  const FETCH_DATA_ERROR_MSG = "Error fetching data or session expired";
  const [isErrorFetch, setIsErrorFetch] = useState(false);

  const PROCESS_DATA_ERROR_MSG = "Error processing data or session expired";
  const [isErrorAddUpdate, setIsErrorAddUpdate] = useState(false);

  const SUCCESS_UPDATE_MSG = "Question edited successfully!";
  const SUCCESS_ADD_MSG =
    "Question added successfully! You can add another question";
  const [isOperationSuccess, setIsOperationSuccess] = useState(false);

  const [isDisable, setIsDisable] = useState(false);

  const [isLoading, setIsLoading] = useState(true);
  const [isLoadingAddUpdate, setIsLoadingAddUpdate] = useState(false);

  const [myQuestionDetails, setMyQuestionDetails] = useState({});

  const [title, setTitle] = useState("");
  const [correct, setCorrect] = useState("");
  const [wrong1, setWrong1] = useState("");
  const [wrong2, setWrong2] = useState("");
  const [wrong3, setWrong3] = useState("");

  useEffect(() => {
    handleGetMyQuestionDetails();
  }, []);

  useEffect(() => {
    if (questionId !== "0" && Object.keys(myQuestionDetails).length !== 0) {
      setTitle(myQuestionDetails.title);
      setCorrect(myQuestionDetails.correct);
      setWrong1(myQuestionDetails.wrong1);
      setWrong2(myQuestionDetails.wrong2);
      setWrong3(myQuestionDetails.wrong3);
    }
  }, [myQuestionDetails]);

  const handleGetMyQuestionDetails = async () => {
    // id 0 means we are adding a question - no need to make a call
    if (questionId !== "0") {
      setIsErrorAddUpdate(false);
      setIsOperationSuccess(false);
      setIsLoading(true);
      await new Promise((resolve) => setTimeout(resolve, 100));
      await axios
        .get(`http://localhost:8080/question/user/${questionId}`, {
          headers: { Authorization: `Bearer ${storeToken}` },
        })
        .then((response) => {
          console.log(response);
          setIsErrorFetch(false);
          setMyQuestionDetails(response.data);
        })
        .catch((error) => {
          console.log(error);
          setIsErrorFetch(true);
        })
        .finally(() => {
          setIsLoading(false);
        });
    } else {
      setIsLoading(false);
    }
  };

  const handleAddQuestionDetails = async () => {
    setIsErrorAddUpdate(false);
    setIsOperationSuccess(false);
    setIsLoadingAddUpdate(true);
    await new Promise((resolve) => setTimeout(resolve, 100));
    await axios
      .post(
        `http://localhost:8080/question/add`,
        {
          title: title,
          correct: correct,
          wrong1: wrong1,
          wrong2: wrong2,
          wrong3: wrong3,
        },
        {
          headers: { Authorization: `Bearer ${storeToken}` },
        }
      )
      .then((response) => {
        console.log(response);
        setIsErrorAddUpdate(false);
        setIsOperationSuccess(true);
      })
      .catch((error) => {
        console.log(error);
        setIsErrorAddUpdate(true);
      })
      .finally(() => {
        setIsLoadingAddUpdate(false);
      });
  };

  const handleUpdateQuestionDetails = async () => {
    setIsErrorAddUpdate(false);
    setIsOperationSuccess(false);
    setIsLoadingAddUpdate(true);
    await new Promise((resolve) => setTimeout(resolve, 100));
    await axios
      .post(
        `http://localhost:8080/question/update`,
        {
          id: questionId,
          title: title,
          correct: correct,
          wrong1: wrong1,
          wrong2: wrong2,
          wrong3: wrong3,
        },
        {
          headers: { Authorization: `Bearer ${storeToken}` },
        }
      )
      .then((response) => {
        console.log(response);
        setIsErrorAddUpdate(false);
        setIsOperationSuccess(true);
        setIsDisable(true);
      })
      .catch((error) => {
        console.log(error);
        setIsErrorAddUpdate(true);
      })
      .finally(() => {
        setIsLoadingAddUpdate(false);
      });
  };

  const handleClear = () => {
    setTitle("");
    setCorrect("");
    setWrong1("");
    setWrong2("");
    setWrong3("");
  };

  const handleOnEnter = (event) => {
    if (event.keyCode === 13) {
      questionId !== "0"
        ? handleUpdateQuestionDetails()
        : handleAddQuestionDetails();
    }
  };

  return (
    <div>
      <Header />
      {isDisable ? (
        <div></div>
      ) : (
        <div className="flex justify-center">
          <div className="mr-5 mt-5 flex justify-center">
            <button
              onClick={handleClear}
              className="text-white bg-blue-500 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm sm:w-auto px-5 py-2.5 text-center"
            >
              Clear
            </button>
          </div>
          {questionId !== "0" ? (
            <div className="mt-5 flex justify-center">
              <button
                onClick={handleGetMyQuestionDetails}
                className="text-white bg-blue-500 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm sm:w-auto px-5 py-2.5 text-center"
              >
                Refresh
              </button>
            </div>
          ) : (
            <div></div>
          )}
        </div>
      )}

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
          {FETCH_DATA_ERROR_MSG}
        </div>
      ) : (
        <div>
          <div className="ml-5 mt-5 mb-2">
            <div className="text-2xl font-bold">Title</div>
          </div>
          <div className="ml-5">
            <input
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              onKeyDownCapture={handleOnEnter}
              className="border rounded-lg border-gray-500 p-1.5 w-1/2"
              disabled={isDisable}
            ></input>
          </div>
          <div className="ml-5 mt-5 mb-2">
            <div className="text-2xl font-bold">Correct answer</div>
          </div>
          <div className="ml-5">
            <input
              type="text"
              value={correct}
              onChange={(e) => setCorrect(e.target.value)}
              onKeyDownCapture={handleOnEnter}
              className="border rounded-lg border-gray-500 p-1.5 w-1/2"
              disabled={isDisable}
            ></input>
          </div>
          <div className="ml-5 mt-5 mb-2">
            <div className="text-2xl font-bold">Wrong answer 1</div>
          </div>
          <div className="ml-5">
            <input
              type="text"
              value={wrong1}
              onChange={(e) => setWrong1(e.target.value)}
              onKeyDownCapture={handleOnEnter}
              className="border rounded-lg border-gray-500 p-1.5 w-1/2"
              disabled={isDisable}
            ></input>
          </div>
          <div className="ml-5 mt-5 mb-2">
            <div className="text-2xl font-bold">Wrong answer 2</div>
          </div>
          <div className="ml-5">
            <input
              type="text"
              value={wrong2}
              onChange={(e) => setWrong2(e.target.value)}
              onKeyDownCapture={handleOnEnter}
              className="border rounded-lg border-gray-500 p-1.5 w-1/2"
              disabled={isDisable}
            ></input>
          </div>
          <div className="ml-5 mt-5 mb-2">
            <div className="text-2xl font-bold">Wrong answer 3</div>
          </div>
          <div className="ml-5">
            <input
              type="text"
              value={wrong3}
              onChange={(e) => setWrong3(e.target.value)}
              onKeyDownCapture={handleOnEnter}
              className="border rounded-lg border-gray-500 p-1.5 w-1/2"
              disabled={isDisable}
            ></input>
          </div>
          {isDisable ? (
            <div></div>
          ) : (
            <div className="m-5 flex">
              <button
                onClick={
                  questionId !== "0"
                    ? handleUpdateQuestionDetails
                    : handleAddQuestionDetails
                }
                className="text-white bg-blue-500 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm sm:w-auto px-5 py-2.5 text-center"
              >
                {questionId !== "0" ? "Edit" : "Add"}
              </button>
            </div>
          )}

          {isLoadingAddUpdate ? (
            <div className="m-5">
              <ProgressBar
                height="80"
                width="80"
                ariaLabel="progress-bar-loading"
                wrapperClass="progress-bar-wrapper"
                borderColor="#1D4ED8"
                barColor="#51E5FF"
              />
            </div>
          ) : isErrorAddUpdate ? (
            <div className="m-5 text-red-600 flex">
              {PROCESS_DATA_ERROR_MSG}
            </div>
          ) : isOperationSuccess ? (
            <div className="m-5 text-green-600 flex">
              {questionId !== "0" ? SUCCESS_UPDATE_MSG : SUCCESS_ADD_MSG}
            </div>
          ) : (
            <div></div>
          )}
        </div>
      )}
    </div>
  );
};

export default MyQuestionDetailsPage;
