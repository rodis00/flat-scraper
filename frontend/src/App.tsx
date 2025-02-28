import { useState } from "react";
import styles from "./App.module.css";
import Flats from "./components/flats/Flats";
import Navbar from "./components/navbar/Navbar";

function App() {
  const [searchBoxValue, setSearchBoxValue] = useState<string | null>(null);

  return (
    <div className={styles.container}>
      <Navbar setSearchBoxValue={setSearchBoxValue} />
      <Flats searchBoxValue={searchBoxValue} />
    </div>
  );
}

export default App;
