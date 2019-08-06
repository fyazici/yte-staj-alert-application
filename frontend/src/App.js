import React, { Component } from 'react';
import './App.css';

import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';

import AlertInput from "./components/AlertInput";
import AlertList from "./components/AlertList";
import ResultChart from './components/ResultChart';

class App extends Component {

  constructor(props) {
    super(props);

    this.state = {
      selectedAlertId: 0
    };

    this.handleAlertSelectionChange = this.handleAlertSelectionChange.bind(this);
  }

  handleAlertSelectionChange = (id) => {
    this.setState({selectedAlertId: id});
  }

  render() {
    return (
      <div className="App">
        <Container>
          <Row>
            <Col>
              <AlertInput />
            </Col>
            <Col>
              <AlertList onAlertSelectionChange={this.handleAlertSelectionChange} />
            </Col>
          </Row>
          <Row>
            <Col>
              <ResultChart alertId={this.state.selectedAlertId} />
            </Col>
          </Row>
        </Container>
      </div>
    );
  }
}

export default App;
