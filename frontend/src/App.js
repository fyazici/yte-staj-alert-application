import React from 'react';
import './App.css';

import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';

import AlertInput from "./components/AlertInput";

function App() {
  return (
    <div className="App">
      <Container>
        <Row>
          <Col>
            <AlertInput />
          </Col>
          <Col>2</Col>
        </Row>
        <Row>
          <Col>3</Col>
        </Row>
      </Container>
    </div>
  );
}

export default App;
