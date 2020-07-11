import React, { useCallback, useState } from "react";
import styled from "styled-components";
import {
  useTextInput,
  TextInputProps,
  useNumeronInput,
  usePassiveData,
  PassiveData,
  NumeronInput,
  MyNameHook,
  useMyNameHook,
} from "../../hooks/formHooks";
import MyName from "./MyName";
import Num from "./Num";

type ContainerProps = unknown; // {};
type Props = {
  type: string;
  content: TextInputProps;
  handleSubmit: () => void;
  passiveData: PassiveData;
  numeronInput: NumeronInput;
  myNameHook: MyNameHook;
}; // {};

const Component: React.FCX<Props> = ({
  className,
  handleSubmit,
  content,
  myNameHook,
  type,
  numeronInput,
  passiveData: { push, opsName, ebs, opsEBs, roomId, result, message },
}) => (
  <div className={className}>
    {roomId && <p>ルームID: {roomId}</p>}
    {result && <p>結果: {result}</p>}
    {opsName && <p>対戦者: {opsName}</p>}
    {opsEBs.map((eb, i) => {
      <p key={i}>
        {eb[0]} → {eb[1]}EAT, {eb[2]}BITE
      </p>;
    })}
    <Num type={type} numeronInput={numeronInput} />
    {ebs.map((eb, i) => {
      <p key={i}>
        {eb[0]} → {eb[1]}EAT, {eb[2]}BITE
      </p>;
    })}
    <MyName activate={type === "enter_room"} myNameHook={myNameHook} />
    {/* <Form onSubmit={handleSubmit}>
      <Form.Field>
        <label>type</label>
        <input placeholder="type" {...type} />
      </Form.Field>
      <Form.Field>
        <label>content</label>
        <input placeholder="Content" {...content} />
      </Form.Field>
      <Form.Field>
        <Checkbox label="I agree to the Terms and Conditions" />
      </Form.Field>
      <Button type="submit">Submit</Button>
    </Form> */}
    <p>
      遊び方: 自動的に相手にマッチングします.
      <ol>
        <li>まず, type: join, content: 自分の名前 と打ち込みましょう</li>
        <li>すると相手に自動的にマッチします, Result欄に, type: set_number, content: 相手の名前 と出れば成功です.</li>
        <li>次に, type: set_number, content: ケタごとに重複のない3ケタの数字を入力しましょう</li>
        <li>相手も入力が完了すると, ゲームが始まります. Result = type: game_start または attack となれば成功です.</li>
        <ol>
          <li>Result = type: attack と表示されたら, type: attack, content: 三桁の数字 を入力しましょう</li>
          <li>
            すると, 次のResult(type: feedback)で, content = (1, 2) などのヒントが得られます.
            <br />
            最初の数字は位置が合っていたケタ数, 次の数字は位置は違うが, その数が含まれている桁数です.
          </li>
          <li>自分が攻撃されたときも同様に Result = type: attacked, content: eat, biteのようになります</li>
          <li>最終的に数をあてたほうが勝ちです. Result = type: result, content win/lose で結果がわかります</li>
        </ol>
      </ol>
    </p>
    )
  </div>
);

const StyledComponent = styled(Component)``;

const Container: React.FC<ContainerProps> = () => {
  console.log("recalled");
  const passiveData = usePassiveData();
  const push = passiveData.push;
  const numeronInput = useNumeronInput(push);
  const myNameHook = useMyNameHook(push);

  const [type, setType] = useTextInput();
  const [content, setContent] = useTextInput();
  const handleSubmit = useCallback(() => {
    push(type.value, content.value);
    setType("");
    setContent("");
  }, [content.value, push, setContent, setType, type.value]);
  return (
    <StyledComponent
      type={passiveData.message?.type ?? "undefined"}
      content={content}
      myNameHook={myNameHook}
      handleSubmit={handleSubmit}
      passiveData={passiveData}
      numeronInput={numeronInput}
    />
  );
};

export default Container;
