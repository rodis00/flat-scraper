import { JSX } from "react";
import styles from "./SearchBox.module.css";

interface SearchBoxProps {
  setSearchBoxValue: (value: string) => void;
}

const SearchBox = ({ setSearchBoxValue }: SearchBoxProps): JSX.Element => {
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchBoxValue(e.target.value);
  };

  return (
    <div className={styles.searchBox}>
      <input
        className={styles.searchInput}
        type="text"
        placeholder="Wyszukaj po adresie..."
        onChange={handleChange}
      />
      <i className="bx bx-search-alt-2"></i>
    </div>
  );
};

export default SearchBox;
