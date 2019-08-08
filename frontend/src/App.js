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
      selectedAlertId: -1,
      alertInputState: false
    };

    this.handleAlertSelectionChange = this.handleAlertSelectionChange.bind(this);
    this.handleAlertInputStateChange = this.handleAlertInputStateChange.bind(this);
  }

  handleAlertSelectionChange = (id) => {
    this.setState({selectedAlertId: id});
  }

  handleAlertInputStateChange = () => {
    this.setState({alertInputState: !this.state.alertInputState});
  }

  render() {
    return (
      <div className="App">
        <Container>
          <Row>
            <Col>
              <AlertInput onAlertInputStateChange={this.handleAlertInputStateChange} />
            </Col>
            <Col>
              <AlertList 
                alertInputState={this.state.alertInputState} 
                onAlertSelectionChange={this.handleAlertSelectionChange} />
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
