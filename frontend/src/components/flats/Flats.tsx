import { JSX, useEffect, useState } from "react";
import Flat from "../flat/Flat";
import FlatInterface from "../interfaces/FlatInterface";
import SortOptions from "../sort/SortOptions";
import styles from "./Flats.module.css";

const accessToken: string =
  "eyJhbGciOiJIUzI1NiJ9.eyJ0eXAiOiJBQ0NFU1MiLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc0MDY3Mjc3OCwiZXhwIjoxNzQwNzU5MTc4fQ.ZWbfIXwGnwbnZF0cXWRFmZHYtkeJtxBItMw49Xlr5CA";

const Flats = (): JSX.Element => {
  const [flats, setFlats] = useState([]);
  const [selectedOption, setSelectedOption] = useState<string | null>(null);

  useEffect(() => {
    const fetchFlats = async () => {
      try {
        let url = "http://localhost:8080/api/v1/flats";
        if (selectedOption) {
          url += `?sort=${selectedOption}`;
        }
        const res = await fetch(url, {
          method: "GET",
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        });
        const data = await res.json();
        setFlats(data.content);
        console.log(data);
      } catch (error) {
        console.error("Error fetching flats:", error);
      }
    };

    fetchFlats();
  }, [selectedOption]);

  return (
    <div className={styles.container}>
      <div className={styles.container__h2}>
        <h2>Mieszkania na sprzedaż w Olsztynie</h2>
        <SortOptions
          selectedOption={selectedOption}
          setSelectedOption={setSelectedOption}
        />
      </div>

      <div className={styles.container__flatList}>
        {flats.map((flat: FlatInterface, index: number) => (
          <li key={`flat-${index}`}>
            <Flat flat={flat} />
          </li>
        ))}
      </div>
    </div>
  );
};

export default Flats;
