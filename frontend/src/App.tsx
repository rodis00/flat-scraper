import styles from "./App.module.css";
import Flats from "./components/flats/Flats";
import Navbar from "./components/navbar/Navbar";

function App() {
  return (
    <div className={styles.container}>
      <Navbar />
      <Flats />
    </div>
  );
}

export default App;
