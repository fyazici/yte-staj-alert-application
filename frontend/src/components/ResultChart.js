import React, { Component } from 'react'
import axios from 'axios'
import Chart from 'react-google-charts';
import { Container, Form, Row, Col, Button } from 'react-bootstrap';
import SockJsClient from 'react-stomp';
import { withTranslation } from 'react-i18next';

class ResultChart extends Component {
    constructor(props) {
        super(props);
        this.state = {
            alertData: {},
            resultData: [],
            resultRetrieveSinceMinutes: 1,
        };
    }

    SuccessChartHeader = [
        { type: "datetime", id: "timestamp", label: this.props.t("resultchart.header.request-time") },
        { type: "number", id: "success", label: this.props.t("resultchart.header.success") }
    ];

    ElapsedChartHeader = [
        { type: "datetime", id: "timestamp", label: this.props.t("resultchart.header.request-time") },
        { type: "number", id: "elapsed", label: this.props.t("resultchart.header.rtt-ms") }
    ];

    componentDidMount() {
        this.retrieveAlertData();
        this.retrieveChartData();
    }

    handleSocketData = (msg) => {
        this.setState({resultData: this.state.resultData.concat([msg])});
    }

    componentDidUpdate(prevProps, prevState) {
        if (this.props !== prevProps) {
            this.retrieveAlertData();
            this.retrieveChartData();
        }

        if (this.state.resultRetrieveSinceMinutes !== prevState.resultRetrieveSinceMinutes) {
            this.retrieveChartData();
        }
    }

    retrieveAlertData = () => {
        axios.get(
            "http://localhost:8080/alert/" + this.props.alertId
        ).then((resp) => {
            this.setState({ alertData: resp.data });
        }).catch((err) => {
            console.error("Error on axios alert get: " + err);
        })
    }

    retrieveChartData = () => {
        if (this.props.alertId !== -1) {
            axios.get(
                "http://localhost:8080/results/" + this.props.alertId,
                { params: { sinceMinutes: this.state.resultRetrieveSinceMinutes } }
            ).then((resp) => {
                this.setState({ resultData: resp.data });
            }).catch((err) => {
                console.error("Error on axios result get: " + err);
            });
        }
    }

    handleRetrieveSinceChange = (event) => {
        this.setState({resultRetrieveSinceMinutes: event.target.value});
    }

    render() {
        const { t } = this.props;
        return (
            <Container className="ResultChartBox">
                <SockJsClient url='http://localhost:8080/alertapplication-ws' topics={['/topic/' + this.props.alertId]}
                    onMessage={ (msg) => this.handleSocketData(msg) }
                    ref={ (client) => { this.clientRef = client }} />
                <h3>{t("resultchart.alertname", { name: this.state.alertData.alertName })}</h3>
                <Chart
                    chartType="ScatterChart"
                    loader={<div>{t("resultchart.loading-message")}</div>}
                    wi
                    data={
                        [this.SuccessChartHeader].concat(this.state.resultData.map((resultElem) => { 
                            return [new Date(resultElem.timestamp), resultElem.success ? 1 : 0]
                        }))
                    }
                    options={{
                        chartArea: { width: "75%" },
                        vAxis: {
                            minValue: 0,
                            maxValue: 1
                        }
                    }}
                />
                <br />
                <Chart
                    chartType="ScatterChart"
                    loader={<div>{t("resultchart.loading-message")}</div>}
                    data={
                        [this.ElapsedChartHeader].concat(this.state.resultData.map((resultElem) => { 
                            return [new Date(resultElem.timestamp), resultElem.elapsed]
                        }))
                    }
                    options={{
                        chartArea: { width: "75%" },
                        vAxis: {
                            minValue: 0,
                            maxValue: 1000
                        }
                    }}
                />
                <br />
                <Form className="pull-right" style={{ margin: "auto" }}>
                    <Form.Group as={Row}>
                        <Form.Label column md style={{textAlign: "right"}}>{t("resultchart.result-since.label")}</Form.Label>
                        <Col md>
                            <Form.Control as="select" 
                                value={this.state.resultRetrieveSinceMinutes} 
                                onChange={this.handleRetrieveSinceChange}>
                                <option value={1}>1 {t("resultchart.result-since.min")}</option>
                                <option value={10}>10 {t("resultchart.result-since.mins")}</option>
                                <option value={30}>30 {t("resultchart.result-since.mins")}</option>
                                <option value={60}>1 {t("resultchart.result-since.hour")}</option>
                                <option value={60 * 6}>6 {t("resultchart.result-since.hours")}</option>
                                <option value={60 * 24}>1 {t("resultchart.result-since.day")}</option>
                                <option value={60 * 24 * 7}>7 {t("resultchart.result-since.days")}</option>
                                <option value={60 * 24 * 30}>30 {t("resultchart.result-since.days")}</option>
                            </Form.Control>
                        </Col>
                        <Col sm>
                            <Button onClick={this.retrieveChartData}>
                            {t("resultchart.result-since.refresh")}
                            </Button>
                        </Col>
                    </Form.Group>
                </Form>
            </Container>
        );
    }
}

export default withTranslation()(ResultChart);