import { JSX, useEffect, useState } from "react";
import Flat from "../flat/Flat";
import FlatInterface from "../interfaces/FlatInterface";
import SortOptions from "../sort/SortOptions";
import styles from "./Flats.module.css";

const accessToken: string =
  "eyJhbGciOiJIUzI1NiJ9.eyJ0eXAiOiJBQ0NFU1MiLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc0MTI0NjA0NCwiZXhwIjoxNzQxMzMyNDQ0fQ.lOHn-Ee6y2xfnu1E_avD5QLw3lbO9ve1DN8JoZyrW9E";

interface FlatsProps {
  searchBoxValue: string | null;
}

const Flats = ({ searchBoxValue }: FlatsProps): JSX.Element => {
  const [flats, setFlats] = useState<FlatInterface[]>([]);
  const [selectedOption, setSelectedOption] = useState<string | null>(null);

  useEffect(() => {
    const fetchFlats = async () => {
      try {
        let url = "http://localhost:8080/api/v1/flats";
        const queryParams: string[] = [];

        if (selectedOption) {
          queryParams.push(`sort=${selectedOption}`);
        }
        if (searchBoxValue) {
          queryParams.push(`search=${searchBoxValue}`);
        }
        if (queryParams.length > 0) {
          url += `?${queryParams.join("&")}`;
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
  }, [selectedOption, searchBoxValue]);

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
        {flats.length > 0 ? (
          flats.map((flat: FlatInterface, index: number) => (
            <li key={`flat-${index}`}>
              <Flat flat={flat} />
            </li>
          ))
        ) : (
          <div className={styles.noContent}>Brak wyników.</div>
        )}
      </div>
    </div>
  );
};

export default Flats;
