import React, { useCallback } from "react";
import styled from "styled-components";
import { useLongPollingObservable, Message } from "../../hooks/long_polling";
import { Form, Checkbox, Button } from "semantic-ui-react";
import { useTextInput, TextInputProps } from "../../hooks/formHooks";

type ContainerProps = unknown; // {};
type Props = {
  v: Message | undefined;
  type: TextInputProps;
  content: TextInputProps;
  handleSubmit: () => void;
}; // {};

const Component: React.FCX<Props> = ({ className, v, handleSubmit, content, type }) => (
  <div className={className}>
    <p>
      Result = id: {v?.id ?? "undefined"} type: {v?.type ?? "undefined"}, content: {v?.content ?? "undefined"}
    </p>
    <Form onSubmit={handleSubmit}>
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
    </Form>
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
  const [v, push] = useLongPollingObservable();
  const [type, setType] = useTextInput();
  const [content, setContent] = useTextInput();
  const handleSubmit = useCallback(() => {
    push(type.value, content.value);
    setType("");
    setContent("");
  }, [content.value, push, setContent, setType, type.value]);
  return <StyledComponent v={v} type={type} content={content} handleSubmit={handleSubmit} />;
};

export default Container;
