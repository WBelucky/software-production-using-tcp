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
        <label>message type</label>
        <input placeholder="type" {...type} />
      </Form.Field>
      <Form.Field>
        <label>message content</label>
        <input placeholder="Content" {...content} />
      </Form.Field>
      <Form.Field>
        <Checkbox label="I agree to the Terms and Conditions" />
      </Form.Field>
      <Button type="submit">Submit</Button>
    </Form>
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
