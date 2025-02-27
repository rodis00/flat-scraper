import React, { JSX } from "react";
import styles from "./SortOptions.module.css";

interface SortOption {
  name: string;
  value: string;
}

interface SortOptionsProps {
  selectedOption: string | null;
  setSelectedOption: (value: string | null) => void;
}

const SortOptions = ({
  selectedOption,
  setSelectedOption,
}: SortOptionsProps): JSX.Element => {
  const options: SortOption[] = [
    { name: "cena", value: "PRICE" },
    { name: "cena/m2", value: "PRICE_PER_METER" },
    { name: "metraż", value: "AREA" },
    { name: "pokoje", value: "ROOMS" },
    { name: "piętro", value: "FLOOR" },
    { name: "rok budowy", value: "YEAR_OF_CONSTRUCTION" },
  ];

  const handleClick = (e: React.MouseEvent<HTMLButtonElement>) => {
    const btnValue = e.currentTarget.value;
    setSelectedOption(selectedOption === btnValue ? null : btnValue);
  };

  return (
    <div className={styles.sortElements}>
      {options.map((option: SortOption, index: number) => (
        <button
          key={`option-${index}`}
          className={`${styles.sortOption} ${
            selectedOption === option.value ? styles.active : ""
          }`}
          value={option.value}
          onClick={handleClick}
        >
          {option.name.toUpperCase()}
        </button>
      ))}
    </div>
  );
};

export default SortOptions;
