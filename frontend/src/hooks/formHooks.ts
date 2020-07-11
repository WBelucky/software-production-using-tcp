import { useState, ChangeEvent, useCallback } from "react";
import { useLongPollingObservable, Message } from "./long_polling";

export const useTextInput = (initialValue?: string) => {
  const [value, setValue] = useState(initialValue ?? "");
  const handleChange = useCallback((e: ChangeEvent<HTMLInputElement>) => {
    setValue(e.target.value);
  }, []);
  return [{ value, onChange: handleChange }, setValue] as const;
};

export type TextInputProps = ReturnType<typeof useTextInput>[0];

export const useNumeronInput = (push: (t: string, c: string) => void) => {
  const [num, setNum] = useState<[number, number, number]>([0, 0, 0]);
  const incrementNum = useCallback((i: number) => {
    setNum((prev) => prev.map((v, j) => (i === j ? v + 1 : v)) as [number, number, number]);
  }, []);
  const decrementNum = useCallback((i: number) => {
    setNum((prev) => prev.map((v, j) => (i === j ? v - 1 : v)) as [number, number, number]);
  }, []);
  const handleSubmit = useCallback(() => {
    push("set_number", num.map((n) => n.toString()).join(""));
  }, [num, push]);
  const handleAttack = useCallback(() => {
    push("attack", num.map((n) => n.toString()).join(""));
  }, [num, push]);
  const invalid = num[0] === num[1] || num[0] === num[2] || num[1] === num[2];
  return { num, incrementNum, decrementNum, handleSubmit, handleAttack, invalid } as const;
};

export const useMyNameHook = (push: (t: string, c: string) => void) => {
  const [myName] = useTextInput();
  const handleSubmit = useCallback(() => {
    push("join", myName.value);
  }, [myName.value, push]);
  return [myName, handleSubmit] as const;
};

export type MyNameHook = ReturnType<typeof useMyNameHook>;

export type NumeronInput = ReturnType<typeof useNumeronInput>;

export const usePassiveData = () => {
  const [opsName, setOpsName] = useState<string>("");
  const [ebs, setEB] = useState<Array<[number, number, number]>>([]);
  const [opsEBs, setOpsEB] = useState<Array<[number, number, number]>>([]);
  const [roomId, setRoomId] = useState("");
  const [result, setResult] = useState<"win" | "lose" | undefined>(undefined);
  const callback = useCallback((m: Message) => {
    switch (m?.type) {
      case "enter_room":
        return;
      case "join":
        setRoomId(m.content);
        return;
      case "set_number":
        setOpsName(m.content);
        return;
      case "game_start":
        return;
      case "attack":
        return;
      case "feedback":
        setOpsEB((prev) => [
          m.content
            .replace(/\s/, "")
            .split(",")
            .map((s) => parseInt(s)) as [number, number, number],
          ...prev,
        ]);
        return;
      case "attacked":
        setEB((prev) => [
          m.content
            .replace(/\s/, "")
            .split(",")
            .map((s) => parseInt(s)) as [number, number, number],
          ...prev,
        ]);
        return;
      case "result":
        setResult(m.content as "win" | "lose");
        return;
      case "resubscribe":
        return;
      default:
        return;
    }
  }, []);
  const [v, push] = useLongPollingObservable(callback);
  return { push, opsName, ebs, opsEBs, roomId, result, message: v } as const;
};

export type PassiveData = ReturnType<typeof usePassiveData>;
