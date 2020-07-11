import React from "react";
import styled from "styled-components";
import { MyNameHook } from "../../hooks/formHooks";
import { Form, Button } from "semantic-ui-react";

type ContainerProps = {
  activate: boolean;
  myNameHook: MyNameHook;
};
type Props = ContainerProps;

const Component: React.FCX<Props> = ({ className, myNameHook: [input, handleSubmit], activate }) => (
  <div className={className}>
    {!activate ? (
      <p>{input.value}</p>
    ) : (
      <Form onSubmit={handleSubmit}>
        <Form.Field>
          <label>YourName</label>
          <input placeholder="Your Name" {...input} />
        </Form.Field>
        <Button type="submit">Submit</Button>
      </Form>
    )}
  </div>
);

const StyledComponent = styled(Component)``;

const Container: React.FC<ContainerProps> = (props) => {
  return <StyledComponent {...props} />;
};

export default Container;
