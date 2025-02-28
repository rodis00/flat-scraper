import { JSX } from "react";
import SearchBox from "../search-box/SearchBox";
import styles from "./Navbar.module.css";

interface NavbarProps {
  setSearchBoxValue: (value: string | null) => void;
}

const Navbar = ({ setSearchBoxValue }: NavbarProps): JSX.Element => {
  const username: string = "John";

  return (
    <div className={styles.nav}>
      <div className={styles.wrapper}>
        <SearchBox setSearchBoxValue={setSearchBoxValue} />
        <div className={styles.user}>
          <span>Witaj, {username}</span>
          <button>wyloguj</button>
        </div>
      </div>
    </div>
  );
};
export default Navbar;
