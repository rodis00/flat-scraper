import "boxicons/css/boxicons.min.css";
import { JSX } from "react";
import FlatInterface from "../interfaces/FlatInterface";
import styles from "./Flat.module.css";

interface FlatProps {
  flatEntity: FlatInterface;
}

const Flat = ({ flatEntity }: FlatProps): JSX.Element => {
  return (
    <div className={styles.flatContainer}>
      <div className={styles.image}>
        <img src={flatEntity.imageUrl} alt="flatEntity image" />
      </div>
      <div className={styles.details}>
        <h2>{flatEntity.price.toLocaleString()} zł</h2>
        <h3>{flatEntity.pricePerMeter.toLocaleString()} zł/m&sup2;</h3>
        <div className={styles.address}>
          <i className="bx bx-map"></i>
          <a
            href={`https://www.google.com/maps/search/?q=${encodeURIComponent(
              flatEntity.address
            )}`}
            target="_blank"
          >
            {flatEntity.address}
          </a>
        </div>
        <div className={styles.area}>
          <i className="bx bx-area"></i>
          <span>{flatEntity.area} m&sup2;</span>
        </div>
        <div className={styles.rooms}>
          <i className="bx bx-door-open"></i>
          <span>{flatEntity.rooms} pokoje</span>
        </div>
        <div className={styles.floor}>
          <i className="bx bx-building"></i>
          <span>{flatEntity.floor > 0 ? `${flatEntity.floor} piętro` : "parter"}</span>
        </div>
      </div>
    </div>
  );
};

export default Flat;
