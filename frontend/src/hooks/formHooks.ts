import { useState, ChangeEvent, useCallback } from "react";

export const useTextInput = (initialValue?: string) => {
  const [value, setValue] = useState(initialValue ?? "");
  const handleChange = useCallback((e: ChangeEvent<HTMLInputElement>) => {
    setValue(e.target.value);
  }, []);
  return [{ value, onChange: handleChange }, setValue] as const;
};

export type TextInputProps = ReturnType<typeof useTextInput>[0];
