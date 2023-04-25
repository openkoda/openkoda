import { useEffect, useRef } from 'react';

// Source: https://reactjs.org/docs/hooks-faq.html
export const usePrevious = <T>(value: T) => {
  const ref = useRef<T>();
  useEffect(() => {
    ref.current = value;
  });
  return ref.current;
};
