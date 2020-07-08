import React from "react";
import styled from "styled-components";
import { useLongPollingObservable } from "../../hooks/long_polling";

type ContainerProps = unknown; // {};
type Props = { v: string | null }; // {};

const Component: React.FCX<Props> = ({ className, v }) => (
  <div className={className}>
    <p>Hello, world {v}</p>
  </div>
);

const StyledComponent = styled(Component)``;

const Container: React.FC<ContainerProps> = () => {
  const v = useLongPollingObservable();
  return <StyledComponent v={v} />;
};

export default Container;
