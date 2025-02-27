import { JSX, useState } from "react";
import styles from "./SearchBox.module.css";

const SearchBox = (): JSX.Element => {
  const [searchBoxValue, setSearchBoxValue] = useState("");

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchBoxValue(e.target.value);
    console.log(searchBoxValue);
  };

  return (
    <div className={styles.searchBox}>
      <input
        className={styles.searchInput}
        type="text"
        placeholder="Wyszukaj..."
        onChange={handleChange}
      />
      <i className="bx bx-search-alt-2"></i>
    </div>
  );
};

export default SearchBox;
