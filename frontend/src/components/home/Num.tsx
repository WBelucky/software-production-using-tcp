import React from "react";
import styled from "styled-components";
import { NumeronInput } from "../../hooks/formHooks";
import { Button } from "semantic-ui-react";

type ContainerProps = {
  type: string;
  numeronInput: NumeronInput;
};
type Props = ContainerProps;

const Component: React.FCX<Props> = ({
  className,
  type,
  numeronInput: { num, incrementNum, decrementNum, handleSubmit, handleAttack, invalid },
}) => (
  <div className={className}>
    <div>
      <div>
        <Button onClick={() => incrementNum(0)}>Submit</Button>
        <p>{num[0]}</p>
        <Button onClick={() => decrementNum(0)}>Submit</Button>
      </div>
      <div>
        <Button onClick={() => incrementNum(1)}>Submit</Button>
        <p>{num[1]}</p>
        <Button onClick={() => decrementNum(1)}>Submit</Button>
      </div>
      <div>
        <Button onClick={() => incrementNum(2)}>Submit</Button>
        <p>{num[2]}</p>
        <Button onClick={() => decrementNum(2)}>Submit</Button>
      </div>
    </div>
    {!invalid && type === "attack" && <Button onClick={handleAttack}>Attack</Button>}
    {!invalid && type === "set_number" && <Button onClick={handleSubmit}>Submit</Button>}
  </div>
);

const StyledComponent = styled(Component)``;

const Container: React.FC<ContainerProps> = (props) => {
  return <StyledComponent {...props} />;
};

export default Container;
