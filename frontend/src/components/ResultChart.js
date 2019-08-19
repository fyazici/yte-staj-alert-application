import React, { Component } from 'react'
import axios from 'axios'
import Chart from 'react-google-charts';
import { Container } from 'react-bootstrap';

class ResultChart extends Component {
    constructor(props) {
        super(props);
        this.state = {
            resultData: [
                this.ResultChartHeader
            ]
        };

        if (!this.props.alertId) {
            this.props.alertId = -1;
        }
    }

    ResultChartHeader = [
        { type: "datetime", id: "timestamp", label: "Request Time" },
        { type: "number", id: "success", label: "Success" }
    ];

    componentDidMount() {
        this.refreshTimer = setInterval(this.retrieveChartData, 1000);
    }

    componentWillUnmount() {
        clearInterval(this.refreshTimer);
    }

    componentDidUpdate(prevProps, prevState) {
        if (this.props !== prevProps) {
            this.retrieveChartData();
        }
    }

    retrieveChartData = () => {
        if (this.props.alertId !== -1) {
            axios.get(
                "http://localhost:8080/results/" + this.props.alertId
            ).then((resp) => {
                var newResults = resp.data.map((elem, index) => {
                    return [new Date(elem.timestamp), elem.success ? 1 : 0];
                });
                var newResultData = [this.ResultChartHeader].concat(newResults);
                this.setState({ resultData: newResultData });
            }).catch((err) => {
                console.error("Error on axios get: " + err);
            });
        }
    }

    render() {
        return (
            <Container className="ResultChartBox">
                <Chart
                    chartType="ScatterChart"
                    loader={<div>Loading Chart</div>}
                    data={this.state.resultData}
                    options={{
                        chart: {
                            title: "Request Results",
                        },
                        vAxis: {
                            minValue: 0,
                            maxValue: 1
                        }
                    }}
                />
            </Container>
        );
    }
}

export default ResultChart;