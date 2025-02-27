import "boxicons/css/boxicons.min.css";
import { JSX } from "react";
import FlatInterface from "../interfaces/FlatInterface";
import styles from "./Flat.module.css";

interface FlatProps {
  flat: FlatInterface;
}

const Flat = ({ flat }: FlatProps): JSX.Element => {
  return (
    <div className={styles.flatContainer}>
      <div className={styles.image}>
        <img src={flat.imageUrl} alt="flat image" />
      </div>
      <div className={styles.details}>
        <h2>{flat.price.toLocaleString()} zł</h2>
        <h3>{flat.pricePerMeter.toLocaleString()} zł/m&sup2;</h3>
        <div className={styles.address}>
          <i className="bx bx-map"></i>
          <a
            href={`https://www.google.com/maps/search/?q=${encodeURIComponent(
              flat.address
            )}`}
            target="_blank"
          >
            {flat.address}
          </a>
        </div>
        <div className={styles.area}>
          <i className="bx bx-area"></i>
          <span>{flat.area} m&sup2;</span>
        </div>
        <div className={styles.rooms}>
          <i className="bx bx-door-open"></i>
          <span>{flat.rooms} pokoje</span>
        </div>
        <div className={styles.floor}>
          <i className="bx bx-building"></i>
          <span>{flat.floor > 0 ? `${flat.floor} piętro` : "parter"}</span>
        </div>
      </div>
    </div>
  );
};

export default Flat;
