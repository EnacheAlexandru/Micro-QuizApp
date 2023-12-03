import React, { useState } from "react";
import Header from "../components/Header";

const NotAnsweredPage = () => {
  const [selectedValues, setSelectedValues] = useState({});

  const handleGroupChange = (groupName, value) => {
    setSelectedValues((prevValues) => ({
      ...prevValues,
      [groupName]: value,
    }));
  };

  const refreshHandle = () => {};

  return (
    <div>
      <Header />
      <div className="m-5 flex justify-center">
        <button
          onClick={refreshHandle}
          className="text-white bg-blue-500 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm sm:w-auto px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
        >
          Refresh
        </button>
      </div>
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
    </div>
  );
};

export default NotAnsweredPage;
