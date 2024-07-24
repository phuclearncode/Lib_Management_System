import React from 'react';
import { Form } from 'react-bootstrap';
import "../../assets/style/Style.css";

const TextInput = ({ label, name, value, onChange, type, placeholder, readOnly }) => {
  const handleChange = (e) => {
    console.log('name:', name, 'value:', e.target.value);
    onChange(e);
  };

  return (
    <Form.Group className="mb-3">
      <Form.Label className="label">{label}</Form.Label>
      <Form.Control
        className="field-input"
        type={type}
        placeholder={placeholder}
        style={{ fontSize: "small" }}
        name={name}
        value={value}
        onChange={handleChange}
        readOnly={readOnly}
      />
    </Form.Group>
  );
};

export default TextInput;
